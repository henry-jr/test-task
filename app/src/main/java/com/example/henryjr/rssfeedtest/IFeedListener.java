package com.example.henryjr.rssfeedtest;

import java.util.List;

public interface IFeedListener<M> {
    void onSuccess(List<M> list);

    void onFailed();
}
