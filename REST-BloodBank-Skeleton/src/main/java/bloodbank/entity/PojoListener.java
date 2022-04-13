/***************************************************************************
 * File: PojoListener.java Course materials (22W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 *
 */
package bloodbank.entity;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class PojoListener {

    @PrePersist
    public void setCreatedOnDate( PojoBase pojo) {
        LocalDateTime now = LocalDateTime.now();
        pojo.setCreated( now);
        pojo.setUpdated( now);
    }

    @PreUpdate
    public void setUpdatedDate( PojoBase pojoBase) {
        pojoBase.setUpdated( LocalDateTime.now());
    }

}