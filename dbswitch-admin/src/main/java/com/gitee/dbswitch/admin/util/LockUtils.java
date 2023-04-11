// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package com.gitee.dbswitch.admin.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public final class LockUtils {

  private static final Cache<String, ReentrantLock> mutexes = CacheBuilder
      .newBuilder()
      .expireAfterWrite(24 * 60L, TimeUnit.MINUTES) // 缓存时间
      .build();

  public static <T> T runInLock(String key, int timeout, TimeUnit timeUnit, Callable<T> callable) {
    ReentrantLock lock = null;
    boolean locked = false;
    try {
      lock = mutexes.get(key, ReentrantLock::new);
      locked = lock.tryLock(timeout, timeUnit);
      if (!locked) {
        throw new RuntimeException("Acquire lock timeout");
      }
      return callable.call();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (lock != null && locked) {
        lock.unlock();
      }
    }
  }

  public static void runInLock(String key, int timeout, TimeUnit timeUnit, Runnable runnable) {
    ReentrantLock lock = null;
    boolean locked = false;
    try {
      lock = mutexes.get(key, ReentrantLock::new);
      locked = lock.tryLock(timeout, timeUnit);
      if (!locked) {
        throw new RuntimeException("Acquire lock timeout");
      }
      runnable.run();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      if (lock != null && locked) {
        lock.unlock();
      }
    }
  }

  private LockUtils() {
  }
}
