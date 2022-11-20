package io.github.xxyopen.mylearn.generator.entitty;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName tele_delivery
 */
@TableName(value ="tele_delivery")
public class TeleDeliveryVO implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Long deliveryNoteId;

    /**
     * 
     */
    private String deliveryName;

    /**
     * 
     */
    private String barcode;

    /**
     * 
     */
    private String datamatric;

    /**
     * 
     */
    private Date deliveryDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    public Long getDeliveryNoteId() {
        return deliveryNoteId;
    }

    /**
     * 
     */
    public void setDeliveryNoteId(Long deliveryNoteId) {
        this.deliveryNoteId = deliveryNoteId;
    }

    /**
     * 
     */
    public String getDeliveryName() {
        return deliveryName;
    }

    /**
     * 
     */
    public void setDeliveryName(String deliveryName) {
        this.deliveryName = deliveryName;
    }

    /**
     * 
     */
    public String getBarcode() {
        return barcode;
    }

    /**
     * 
     */
    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    /**
     * 
     */
    public String getDatamatric() {
        return datamatric;
    }

    /**
     * 
     */
    public void setDatamatric(String datamatric) {
        this.datamatric = datamatric;
    }

    /**
     * 
     */
    public Date getDeliveryDate() {
        return deliveryDate;
    }

    /**
     * 
     */
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TeleDeliveryVO other = (TeleDeliveryVO) that;
        return (this.getDeliveryNoteId() == null ? other.getDeliveryNoteId() == null : this.getDeliveryNoteId().equals(other.getDeliveryNoteId()))
            && (this.getDeliveryName() == null ? other.getDeliveryName() == null : this.getDeliveryName().equals(other.getDeliveryName()))
            && (this.getBarcode() == null ? other.getBarcode() == null : this.getBarcode().equals(other.getBarcode()))
            && (this.getDatamatric() == null ? other.getDatamatric() == null : this.getDatamatric().equals(other.getDatamatric()))
            && (this.getDeliveryDate() == null ? other.getDeliveryDate() == null : this.getDeliveryDate().equals(other.getDeliveryDate()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getDeliveryNoteId() == null) ? 0 : getDeliveryNoteId().hashCode());
        result = prime * result + ((getDeliveryName() == null) ? 0 : getDeliveryName().hashCode());
        result = prime * result + ((getBarcode() == null) ? 0 : getBarcode().hashCode());
        result = prime * result + ((getDatamatric() == null) ? 0 : getDatamatric().hashCode());
        result = prime * result + ((getDeliveryDate() == null) ? 0 : getDeliveryDate().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", deliveryNoteId=").append(deliveryNoteId);
        sb.append(", deliveryName=").append(deliveryName);
        sb.append(", barcode=").append(barcode);
        sb.append(", datamatric=").append(datamatric);
        sb.append(", deliveryDate=").append(deliveryDate);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}