package com.fh.util;

import org.apache.poi.ss.util.CellRangeAddress;

public class CellRangeAddressWrapper implements Comparable<CellRangeAddressWrapper>  {
    public CellRangeAddress range;

    public CellRangeAddressWrapper(CellRangeAddress theRange) {
        this.range = theRange;
    }

    public int compareTo(CellRangeAddressWrapper craw) {
        if (range.getFirstColumn() < craw.range.getFirstColumn()
                && range.getFirstRow() < craw.range.getFirstRow()) {
            return -1;
        } else if (range.getFirstColumn() == craw.range.getFirstColumn()
                && range.getFirstRow() == craw.range.getFirstRow()) {
            return 0;
        } else {
            return 1;
        }
    }

}
