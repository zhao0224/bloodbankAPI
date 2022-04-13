/***************************************************************************
 * File: PojoListener.java Course materials (22W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 */
package bloodbank.entity;

import static java.time.LocalDateTime.now;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class PojoCompositeListener {

    @PrePersist
    public void setCreatedOnDate( PojoBaseCompositeKey< ?> pojoBaseComposite) {
        pojoBaseComposite.setCreated(now());
        pojoBaseComposite.setUpdated(now());
    }

    @PreUpdate
    public void setUpdatedDate( PojoBaseCompositeKey< ?> pojoBaseComposite) {
        pojoBaseComposite.setUpdated(now());
    }

}