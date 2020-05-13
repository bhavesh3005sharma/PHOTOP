package com.example.bestphotocollections.Model;

public class ItemData {
    private String mMatadata;
    private String mUri;
    private String mtitle;
    private String  key;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public ItemData(){

    }

    public void setmMatadata(String mMatadata) {
        this.mMatadata = mMatadata;
    }

    public void setmUri(String mUri) {
        this.mUri = mUri;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public String getmMatadata() {
        return mMatadata;
    }

    public String getmUri() {
        return mUri;
    }

    public String getMtitle() {
        return mtitle;
    }

    public ItemData(String mMatadata, String mUri, String mtitle) {
        this.mMatadata = mMatadata;
        this.mUri = mUri;
        this.mtitle = mtitle;
    }
}
