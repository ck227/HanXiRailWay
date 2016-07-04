package com.cnbs.entity;

/**
 * Created by Administrator on 2016/1/20.
 */
public class DeleteDb {


    /**
     * id : 8825
     * filename : 15_2
     * filetype : delete
     * filetypeexplain : 删除
     * typeId : 1
     */

    private String id;
    private String filename;
    private String filetype;
    private String filetypeexplain;
    private String typeId;

    public void setId(String id) {
        this.id = id;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setFiletype(String filetype) {
        this.filetype = filetype;
    }

    public void setFiletypeexplain(String filetypeexplain) {
        this.filetypeexplain = filetypeexplain;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getId() {
        return id;
    }

    public String getFilename() {
        return filename;
    }

    public String getFiletype() {
        return filetype;
    }

    public String getFiletypeexplain() {
        return filetypeexplain;
    }

    public String getTypeId() {
        return typeId;
    }
}
