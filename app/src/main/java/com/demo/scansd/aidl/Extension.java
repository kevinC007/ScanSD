package com.demo.scansd.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Scott on 2016/5/25.
 */
public class Extension implements Parcelable{
    String extensionName;
    int count;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getExtensionName() {
        return extensionName;
    }

    public void setExtensionName(String extensionName) {
        this.extensionName = extensionName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(extensionName);
        dest.writeInt(count);
    }

    public static final Parcelable.Creator<Extension> CREATOR = new Creator<Extension>() {
        @Override
        public Extension createFromParcel(Parcel source) {
            String name = source.readString();
            int count = source.readInt();
            Extension extension = new Extension();
            extension.setExtensionName(name);
            extension.setCount(count);
            return extension;
        }

        @Override
        public Extension[] newArray(int size) {
            return new Extension[size];
        }
    };
}
