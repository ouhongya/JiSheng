package com.fh.entity.app;

import java.util.List;

/**
 * 问题检查任务
 * {
 * "row":"id",
 *   "mark": [
 *     {
 *      "content": "出问题的文字",
 *       "type" : "1加分2减分",
 *       "score" : "分值",
 *       "status" : "问题严重性:1一般,2严重",
 *       "remark" : "备注",
 *       "images" : [
 *         {"url": "路径","id": "图片id"}
 *       ]
 *     }
 *   ]
 * }
 * @return
 */
public class IssueCheckingVo {
    private String row;
    private String detail_id;
    private String new_score;
    private List<MarkVo> mark;
    private boolean status;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getNew_score() {
        return new_score;
    }

    public void setNew_score(String new_score) {
        this.new_score = new_score;
    }

    public String getDetail_id() {
        return detail_id;
    }

    public void setDetail_id(String detail_id) {
        this.detail_id = detail_id;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public List<MarkVo> getMark() {
        return mark;
    }

    public void setMark(List<MarkVo> mark) {
        this.mark = mark;
    }
}
