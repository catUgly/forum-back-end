package net.sunofbeach.blog.services;

import net.sunofbeach.blog.pojo.Looper;
import net.sunofbeach.blog.response.Result;

public interface LooperService {

    Result addLooper(Looper looper);

    Result deleteLooper(String looperId);

    Result updateLooper(String looperId, Looper looper);

    Result looperList();

    Result getLooper(String looperId);
}
