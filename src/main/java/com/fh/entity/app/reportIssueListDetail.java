package com.fh.entity.app;

import java.util.List;

public class reportIssueListDetail {
    private String id;
    private String rectifyMeasures;
    private String rectifyRemark;
    private List<String> img;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRectifyMeasures() {
        return rectifyMeasures;
    }

    public void setRectifyMeasures(String rectifyMeasures) {
        this.rectifyMeasures = rectifyMeasures;
    }

    public String getRectifyRemark() {
        return rectifyRemark;
    }

    public void setRectifyRemark(String rectifyRemark) {
        this.rectifyRemark = rectifyRemark;
    }

    public List<String> getImg() {
        return img;
    }

    public void setImg(List<String> img) {
        this.img = img;
    }
}
