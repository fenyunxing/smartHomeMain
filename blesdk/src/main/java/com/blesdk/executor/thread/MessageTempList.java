package com.blesdk.executor.thread;

import java.util.ArrayList;
import java.util.List;

/**
 * 缓存信息列表类
 *
 * Created by admin on 2016/10/20.
 */
public class MessageTempList {
    protected List<byte[]> datas = new ArrayList();

    /**
     * 添加字节数组到缓存列表中
     * @param prambyte
     */
    protected synchronized void setByteList(byte[] prambyte) {
        this.datas.add(prambyte);
    }

    /**
     * 得到列表第一行数据
     * @return
     */
    protected synchronized byte[] getByteList() {
        if (this.datas.size() != 0) {
            return (byte[]) this.datas.get(0);
        } else {
            return null;
        }

    }

    /**
     * 删除列表第一行数据
     */
    protected synchronized void delByteList() {
        if (this.datas.size() != 0) {
            this.datas.remove(0);
        }
    }

    /**
     * 得到缓存列表存储的数据数量
     * @return 缓存数量
     */
    protected int getSize() {
        if (this.datas.size() != 0) {
            return this.datas.get(0).length;
        } else {
            return 0;
        }
    }
}
