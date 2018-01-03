package com.demo.scansd.aidl;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Scott on 2016/5/25.
 */

public class BiggestFile implements Parcelable {
    String filePath;
    long fileLength;

    public BiggestFile(String filePath, long fileLength){
        super();
        this.filePath = filePath;
        this.fileLength = fileLength;
    }

    private BiggestFile(Parcel source){
        fileLength = source.readLong();
        filePath = source.readString();
    }

    public long getFileLength() {
        return fileLength;
    }

    public String getFilePath() {
        return filePath;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(filePath);
        dest.writeLong(fileLength);
    }

    public static final Parcelable.Creator<BiggestFile> CREATOR = new Creator<BiggestFile>() {
        @Override
        public BiggestFile createFromParcel(Parcel source) {
            return new BiggestFile(source);
        }

        @Override
        public BiggestFile[] newArray(int size) {
            return new BiggestFile[size];
        }
    };
}
