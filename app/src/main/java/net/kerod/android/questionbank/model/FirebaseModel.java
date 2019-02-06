package net.kerod.android.questionbank.model;


import net.kerod.android.questionbank.utility.Constants;

import java.util.Comparator;


public abstract class FirebaseModel<I> implements Comparable<FirebaseModel<I>> {
    public static final String FIELD_UID = "uid";
    public static final String FIELD_SORT_ORDER = "sortOrder";
    protected String uid;
    protected Double sortOrder;
    //adaptors may have a header
    //protected int viewType = Constants.ADAPTER_VIEW_TYPE_BODY;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Double getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Double sortOrder) {
        this.sortOrder = sortOrder;
    }


    /**
     * We may want to sort the models locally
     */
    @Override
    public int compareTo(FirebaseModel<I> o) {
        if (getSortOrder() == null) {
            return -1;
        } else if (o == null || o.getSortOrder() == null) {
            return 1;
        }
        return getSortOrder().compareTo(o.getSortOrder());
    }
}
