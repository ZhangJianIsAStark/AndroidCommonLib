package stark.a.is.zhang.photogallery.model;

import java.util.List;

public class Response {
    private String queryEnc;
    private String queryExt;
    private String listNum;
    private String displayNum;
    private String gsm;
    private String bdSearchTime;
    private String isNeedAsyncRequest;
    private String bdIsClustered;
    private List<Data> data;

    public String getQueryEnc() {
        return queryEnc;
    }

    public void setQueryEnc(String queryEnc) {
        this.queryEnc = queryEnc;
    }

    public String getQueryExt() {
        return queryExt;
    }

    public void setQueryExt(String queryExt) {
        this.queryExt = queryExt;
    }

    public String getListNum() {
        return listNum;
    }

    public void setListNum(String listNum) {
        this.listNum = listNum;
    }

    public String getDisplayNum() {
        return displayNum;
    }

    public void setDisplayNum(String displayNum) {
        this.displayNum = displayNum;
    }

    public String getGsm() {
        return gsm;
    }

    public void setGsm(String gsm) {
        this.gsm = gsm;
    }

    public String getBdSearchTime() {
        return bdSearchTime;
    }

    public void setBdSearchTime(String bdSearchTime) {
        this.bdSearchTime = bdSearchTime;
    }

    public String getIsNeedAsyncRequest() {
        return isNeedAsyncRequest;
    }

    public void setIsNeedAsyncRequest(String isNeedAsyncRequest) {
        this.isNeedAsyncRequest = isNeedAsyncRequest;
    }

    public String getBdIsClustered() {
        return bdIsClustered;
    }

    public void setBdIsClustered(String bdIsClustered) {
        this.bdIsClustered = bdIsClustered;
    }

    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
