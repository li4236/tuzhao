package com.tuzhao.publicmanager;

import android.util.Log;

import com.tuzhao.info.CollectionInfo;

import java.util.List;

/**
 * Created by TZL12 on 2017/7/4.
 * 单例，用来管理用户收藏记录信息
 */

public class CollectionManager {

    private static CollectionManager collectionManager = null;
    private List<CollectionInfo> collection_datas = null;

    public static CollectionManager getInstance() {

        if (collectionManager == null) {

            synchronized (CollectionManager.class) {

                if (collectionManager == null) {

                    collectionManager = new CollectionManager();
                }
                return collectionManager;
            }
        } else {

            return collectionManager;
        }
    }

    public boolean hasCollectionData() {
        return collection_datas != null;
    }

    /**
     * 获得用户收藏记录
     */
    public List<CollectionInfo> getCollection_datas() {
        return collection_datas;
    }

    /**
     * 设置用户收藏记录
     */
    public void setCollection_datas(List<CollectionInfo> collection_datas) {
        this.collection_datas = collection_datas;
    }

    public void addCollectionData(CollectionInfo collectionInfo) {
        collection_datas.add(collectionInfo);
        Log.e("TAG", "addCollectionData: " + collectionInfo);
    }

    public CollectionInfo getCollection(String parkLotId) {
        for (CollectionInfo collectionInfo : collection_datas) {
            if (collectionInfo.getParkspace_id().equals(parkLotId) && collectionInfo.getType().equals("1")) {
                return collectionInfo;
            }
        }
        return null;
    }

    public void removeCollection(CollectionInfo collection) {
        if (collection_datas.contains(collection)) {
            collection_datas.remove(collection);
        }
    }

    /**
     * 是否收藏了该停车位所在的车场
     */
    public boolean isContainParkLot(String parkLotId) {
        for (CollectionInfo collectionInfo : collection_datas) {
            if (collectionInfo.getParkspace_id().equals(parkLotId) && collectionInfo.getType().equals("1")) {
                return true;
            }
        }
        return false;
    }

    /**
     * 在收藏记录中查找是否已存在一个收藏记录
     */
    public MessageHolder checkCollectionDatas(String belong_id, String is_charge) {
        boolean iSExist = false;
        int pOsition = -1;
        MessageHolder holder = new MessageHolder();

        if (hasCollectionData()) {
            for (int i = 0; i < collection_datas.size(); i++) {
                if (is_charge.equals("2")) {
                    if (collection_datas.get(i).getType().equals(is_charge)) {
                        if (collection_datas.get(i).getChargestation_id().equals(belong_id)) {
                            iSExist = true;
                            pOsition = i;
                            break;
                        }
                    }
                } else if (is_charge.equals("1")) {
                    if (collection_datas.get(i).getType().equals(is_charge)) {
                        if (collection_datas.get(i).getParkspace_id().equals(belong_id)) {
                            iSExist = true;
                            pOsition = i;
                            break;
                        }
                    }
                } else if (is_charge.equals("3")) {
                    if (collection_datas.get(i).getType().equals(is_charge)) {
                        if (collection_datas.get(i).getPlace().equals(belong_id)) {
                            iSExist = true;
                            pOsition = i;
                            break;
                        }
                    }
                }
            }
        }
        holder.isExist = iSExist;
        holder.position = pOsition;

        return holder;
    }

    /**
     * 取消一个收藏记录
     */
    public void removeOneCollection(int position) {
        if (collection_datas != null) {
            collection_datas.remove(position);
        }
    }

    /**
     * 根据提供的收藏id来取消一个收藏记录
     */
    public void removeOneCollectionByBelongId(String collection_id) {
        for (int i = 0; i < collection_datas.size(); i++) {
            if (collection_datas.get(i).getId().equals(collection_id)) {
                collection_datas.remove(i);
                break;
            }
        }
    }

    public class MessageHolder {
        public boolean isExist;
        public int position;
    }
}
