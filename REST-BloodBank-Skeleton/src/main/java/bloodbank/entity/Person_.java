package bloodbank.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-03-30T18:46:25.432-0400")
@StaticMetamodel(Person.class)
public class Person_ extends PojoBase_ {
	public static volatile SingularAttribute<Person, String> firstName;
	public static volatile SingularAttribute<Person, String> lastName;
	public static volatile SetAttribute<Person, DonationRecord> donations;
	public static volatile SetAttribute<Person, Contact> contacts;
}
