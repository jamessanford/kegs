#!/usr/bin/env python

# This makes a decent icon for 'dial.png'.
# convert -background black -transparent black -resize 80x80 noname.svg noname.png

import os
import svgwrite

dwg = svgwrite.Drawing(filename='dial.svg')
dwg.viewbox(minx=-200, miny=-200, width=400, height=400)
doc = dwg.g()

chevron = dwg.polygon(points=[(-5,-5), (5,-5), (4, 5), (-4, 5)], fill='white', opacity=0.5)
chevron.scale(1, 1.5)
chevron.scale(2, 2)
chevron.scale(1, 1.25)
dwg.defs.add(chevron)

center = dwg.circle(center=(0, 0), r=40, fill='white', opacity=0.5)
dwg.defs.add(center)

count = 5
diff = 90/count
angles = []
for q in range(count + 1):
  angles.append(-90 + (diff * q))
#print angles

for r in angles:
  # Try skipping the -90/90
#  if r == -90:
#    continue

  ux = dwg.use(chevron)
  ux.rotate(r)
  ux.translate(0, -160)
  doc.add(ux)

  if r == 0:
    # Don't draw it twice at -0 and 0
    continue

  u2 = dwg.use(chevron)
  u2.rotate(abs(r))
  u2.translate(0, -160)
  doc.add(u2)

u_c = dwg.use(center)
u_c.translate(0, -25)
doc.add(u_c)

dark_red = '#707070'  # actually gray.
size = 14.25
right = 27
needle2 = dwg.g()
needle2.add(dwg.circle(center=(-75, 0), r=size, fill=dark_red))
needle2.add(dwg.circle(center=(right, 0), r=size, fill=dark_red))
needle2.add(dwg.polygon(points=[(-75, size), (-75, -size), (right, -size), (right, size)], fill=dark_red))
needle2.add(dwg.circle(center=(-75, 0), r=6, fill='black'))
dwg.defs.add(needle2)

u_n2 = dwg.use(needle2)
u_n2.translate(57, -78 + 5)
u_n2.rotate(-40)
doc.add(u_n2)

# calibration circle for center of dial
#doc.add(dwg.circle(center=(0, -25), r=10, fill='yellow'))

# For testing, change the opacity of this to 1.0 for easier viewing.
dwg.add(dwg.polygon(points=[(-200, -200), (-200, 200), (200, 200), (200, -200)], fill='black', opacity=0.0))

doc.translate(0, 70)
dwg.add(doc)
dwg.save()
print '...wrote dial.svg'

CONVERT = 'convert -background black -transparent black -resize 80x80 dial.svg dial.png'
r = os.system(CONVERT)
if r == 0:
  print '...wrote dial.png'
else:
  print 'unable to create dial.png from dial.svg'
  print 'you should run\n   ', CONVERT

print 'you should put dial.png into res/drawable-*'
