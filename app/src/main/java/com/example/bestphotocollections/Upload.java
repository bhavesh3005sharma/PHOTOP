package com.example.bestphotocollections;

public class Upload {
    private  String mtitle;
    private  String mMatadata;
    private  String mUri;

    public Upload() {
        //empty constructor needed
    }

    public String getMtitle() {
        return mtitle;
    }

    public String getmMatadata() {
        return mMatadata;
    }

    public String getmUri() {
        return mUri;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public void setmMatadata(String mMatadata) {
        this.mMatadata = mMatadata;
    }

    public void setmUri(String mUri) {
        this.mUri = mUri;
    }

    public Upload(String title, String metadata , String imageUrl) {
        if (title.trim().equals("")) {
            mtitle = "NO TITLE";
        }
        if (metadata.trim().equals("")) {
            mMatadata = "NO METADATA";
        }

        mMatadata = metadata;
        mtitle = title;
        mUri = imageUrl;
    }


}


