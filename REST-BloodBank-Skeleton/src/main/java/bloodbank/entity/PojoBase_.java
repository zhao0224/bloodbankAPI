package bloodbank.entity;

import java.time.LocalDateTime;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-03-30T18:46:25.436-0400")
@StaticMetamodel(PojoBase.class)
public class PojoBase_ {
	public static volatile SingularAttribute<PojoBase, Integer> id;
	public static volatile SingularAttribute<PojoBase, Integer> version;
	public static volatile SingularAttribute<PojoBase, LocalDateTime> created;
	public static volatile SingularAttribute<PojoBase, LocalDateTime> updated;
}
