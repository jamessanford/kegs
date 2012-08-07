package com.froop.app.kegs;

import java.util.ArrayList;

class KeyTable {
  static class A2Key {
    public A2Key(int keycode, boolean use_shift) {
      this.keycode = keycode;
      this.use_shift = use_shift;
    }
    public int keycode;
    public boolean use_shift;
  }

  // Mapping of ASCII 0x20-0x7e inclusive to the ADB keycode.
  // 0x20 is element 0.
  static ArrayList<A2Key> ascii = new ArrayList<A2Key>(96);
  static {
    ascii.add(new A2Key(0x31, false)); // <space>
    ascii.add(new A2Key(0x12, true));  // !
    ascii.add(new A2Key(0x27, true));  // "
    ascii.add(new A2Key(0x14, true));  // #
    ascii.add(new A2Key(0x15, true));  // $
    ascii.add(new A2Key(0x17, true));  // %
    ascii.add(new A2Key(0x1a, true));  // &
    ascii.add(new A2Key(0x27, false)); // '
    ascii.add(new A2Key(0x19, true));  // (
    ascii.add(new A2Key(0x1d, true));  // )
    ascii.add(new A2Key(0x1c, true));  // *
    ascii.add(new A2Key(0x18, true));  // +
    ascii.add(new A2Key(0x2b, false)); // ,
    ascii.add(new A2Key(0x1b, false)); // -
    ascii.add(new A2Key(0x2f, false)); // .
    ascii.add(new A2Key(0x2c, false)); // /
    ascii.add(new A2Key(0x1d, false)); // 0
    ascii.add(new A2Key(0x12, false)); // 1
    ascii.add(new A2Key(0x13, false)); // 2
    ascii.add(new A2Key(0x14, false)); // 3
    ascii.add(new A2Key(0x15, false)); // 4
    ascii.add(new A2Key(0x17, false)); // 5
    ascii.add(new A2Key(0x16, false)); // 6
    ascii.add(new A2Key(0x1a, false)); // 7
    ascii.add(new A2Key(0x1c, false)); // 8
    ascii.add(new A2Key(0x19, false)); // 9
    ascii.add(new A2Key(0x29, true));  // :
    ascii.add(new A2Key(0x29, false)); // ;
    ascii.add(new A2Key(0x2b, true));  // <
    ascii.add(new A2Key(0x18, false)); // =
    ascii.add(new A2Key(0x2f, true));  // >
    ascii.add(new A2Key(0x2c, true));  // ?
    ascii.add(new A2Key(0x13, true));  // @
    ascii.add(new A2Key(0x00, true));  // A
    ascii.add(new A2Key(0x0b, true));  // B
    ascii.add(new A2Key(0x08, true));  // C
    ascii.add(new A2Key(0x02, true));  // D
    ascii.add(new A2Key(0x0e, true));  // E
    ascii.add(new A2Key(0x03, true));  // F
    ascii.add(new A2Key(0x05, true));  // G
    ascii.add(new A2Key(0x04, true));  // H
    ascii.add(new A2Key(0x22, true));  // I
    ascii.add(new A2Key(0x26, true));  // J
    ascii.add(new A2Key(0x28, true));  // K
    ascii.add(new A2Key(0x25, true));  // L
    ascii.add(new A2Key(0x2e, true));  // M
    ascii.add(new A2Key(0x2d, true));  // N
    ascii.add(new A2Key(0x1f, true));  // O
    ascii.add(new A2Key(0x23, true));  // P
    ascii.add(new A2Key(0x0c, true));  // Q
    ascii.add(new A2Key(0x0f, true));  // R
    ascii.add(new A2Key(0x01, true));  // S
    ascii.add(new A2Key(0x11, true));  // T
    ascii.add(new A2Key(0x20, true));  // U
    ascii.add(new A2Key(0x09, true));  // V
    ascii.add(new A2Key(0x0d, true));  // W
    ascii.add(new A2Key(0x07, true));  // X
    ascii.add(new A2Key(0x10, true));  // Y
    ascii.add(new A2Key(0x06, true));  // Z
    ascii.add(new A2Key(0x21, false)); // [
    ascii.add(new A2Key(0x2a, false)); // \
    ascii.add(new A2Key(0x1e, false)); // ]
    ascii.add(new A2Key(0x16, true));  // ^
    ascii.add(new A2Key(0x1b, true));  // _
    ascii.add(new A2Key(0x32, false)); // `
    ascii.add(new A2Key(0x00, false)); // a
    ascii.add(new A2Key(0x0b, false)); // b
    ascii.add(new A2Key(0x08, false)); // c
    ascii.add(new A2Key(0x02, false)); // d
    ascii.add(new A2Key(0x0e, false)); // e
    ascii.add(new A2Key(0x03, false)); // f
    ascii.add(new A2Key(0x05, false)); // g
    ascii.add(new A2Key(0x04, false)); // h
    ascii.add(new A2Key(0x22, false)); // i
    ascii.add(new A2Key(0x26, false)); // j
    ascii.add(new A2Key(0x28, false)); // k
    ascii.add(new A2Key(0x25, false)); // l
    ascii.add(new A2Key(0x2e, false)); // m
    ascii.add(new A2Key(0x2d, false)); // n
    ascii.add(new A2Key(0x1f, false)); // o
    ascii.add(new A2Key(0x23, false)); // p
    ascii.add(new A2Key(0x0c, false)); // q
    ascii.add(new A2Key(0x0f, false)); // r
    ascii.add(new A2Key(0x01, false)); // s
    ascii.add(new A2Key(0x11, false)); // t
    ascii.add(new A2Key(0x20, false)); // u
    ascii.add(new A2Key(0x09, false)); // v
    ascii.add(new A2Key(0x0d, false)); // w
    ascii.add(new A2Key(0x07, false)); // x
    ascii.add(new A2Key(0x10, false)); // y
    ascii.add(new A2Key(0x06, false)); // z
    ascii.add(new A2Key(0x21, true));  // {
    ascii.add(new A2Key(0x2a, true));  // |
    ascii.add(new A2Key(0x1e, true));  // }
    ascii.add(new A2Key(0x32, true));  // ~
  }
}
