package com.solinftec.exemploleituraescrita.util;

import android.hardware.usb.UsbDevice;

import java.util.List;

public interface HomeCallback {
    public List<UsbDevice> getUsbDevices();
}
