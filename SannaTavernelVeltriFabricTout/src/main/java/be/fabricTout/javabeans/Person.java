package be.fabricTout.javabeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.json.JSONArray;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.IntSequenceGenerator.class,
        property = "KeyPerson",
        scope = Person.class
    )
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    // ATTRIBUTES
    protected int idPerson;
    protected String firstName;
    protected String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    protected LocalDate birthDate;
    protected String phoneNumber;

    // CONSTRUCTORS
    public Person() {}

    public Person(int idPerson, String firstName, String lastName, LocalDate birthDate, String phoneNumber) {
    	setIdPerson(idPerson);
    	setFirstName(firstName);
    	setLastName(lastName);
    	setBirthDate(birthDate);
    	setPhoneNumber(phoneNumber);
    }
    
	public Person(String firstName, String lastName, LocalDate birthDate, String phoneNumber) {
		this(-1, firstName, lastName, birthDate, phoneNumber);
	}
	
	public Person(JSONObject json) {
	    this();

	    if (json.has("idPerson")) {
	    	if (!json.optString("idPerson").isBlank()) 
	    		setIdPerson(json.optInt("idPerson"));
	    }
	    if (json.has("firstName")) {
	    	if (!json.optString("firstName").isBlank())
	    		setFirstName(json.optString("firstName"));
	    }
	    if (json.has("lastName")) {
	    	if (!json.optString("lastName").isBlank())
	    		setLastName(json.optString("lastName"));
	    }
	    if (json.has("birthDate")) {
	    	if (!json.optString("birthDate").isBlank())
	    	{
		        Object dateObject = json.get("birthDate");
		        if (dateObject instanceof JSONArray) {
		            JSONArray dateArray = (JSONArray) dateObject;
		            if (dateArray.length() == 3) { 
		                int year = dateArray.optInt(0, 0);
		                int month = dateArray.optInt(1, 1);
		                int day = dateArray.optInt(2, 1);
		                setBirthDate(LocalDate.of(year, month, day));
		            }
		        } else if (dateObject instanceof String) {
		            try {
		            	setBirthDate(LocalDate.parse((String) dateObject));
		            } catch (Exception e) {
		                System.err.println("Erreur lors de l'analyse de la date : " + dateObject);
		                e.printStackTrace();
		            }
		        }
	    	}
	    }

	    if (json.has("phoneNumber")) {
	    	if (!json.optString("phoneNumber").isBlank())
	    		setPhoneNumber(json.optString("phoneNumber"));
	    }
	}


    // PROPERTIES
	public int getIdPerson() {
		return idPerson;
	}
	
	public void setIdPerson(int idPerson) {
	    String idString = String.valueOf(idPerson); 
	    if (!idString.matches("-?\\d+")) { 
	        throw new IllegalArgumentException("idPerson must be a valid integer.");
	    }
	    this.idPerson = idPerson;
	}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
    	if (firstName == null)
    	{
			throw new IllegalArgumentException("firstName cannot be null.");
		}
		if (firstName.isEmpty()) {
			throw new IllegalArgumentException("firstName cannot be empty.");
    	}
		if (firstName.isBlank()) {
			throw new IllegalArgumentException("firstName cannot be blank.");
		}
		if (firstName.length() > 50) {
			throw new IllegalArgumentException("firstName cannot be longer than 50 characters.");
		}
		if (!firstName.matches("[a-zA-Z]+")) {
			throw new IllegalArgumentException("firstName must contain only letters.");
		}
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
    	if (lastName == null){
            throw new IllegalArgumentException("lastName cannot be null.");
    	}
        if (lastName.isEmpty()) {
            throw new IllegalArgumentException("lastName cannot be empty.");           
        }
		if (lastName.isBlank()) {
			throw new IllegalArgumentException("lastName cannot be blank.");
		}
		if (lastName.length() > 50) {
			throw new IllegalArgumentException("lastName cannot be longer than 50 characters.");
		}
		if (!lastName.matches("[a-zA-Z]+")) {
			throw new IllegalArgumentException("lastName must contain only letters.");
		}
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
    	if (birthDate == null) {
            throw new IllegalArgumentException("birthDate cannot be null.");
        }
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("birthDate cannot be in the future.");
        }
        this.birthDate = birthDate;
    }

    public String getBirthDateAsString() {
        return birthDate != null ? birthDate.format(DateTimeFormatter.ISO_DATE) : null;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
		if (phoneNumber == null) {
			throw new IllegalArgumentException("phoneNumber cannot be null.");
		}
		if (phoneNumber.isEmpty()) {
			throw new IllegalArgumentException("phoneNumber cannot be empty.");
		}
		if (phoneNumber.isBlank()) {
			throw new IllegalArgumentException("phoneNumber cannot be blank.");
		}
		if (phoneNumber.length() > 20) {
			throw new IllegalArgumentException("phoneNumber cannot be longer than 20 characters.");
		}
		if (!phoneNumber.matches("^\\+?[0-9\\-\\s]{10,15}$")) {
            throw new IllegalArgumentException("phoneNumber must be a valid format (10-15 digits, optional +, "
            		+ "spaces, or hyphens).");
        }
        this.phoneNumber = phoneNumber;
    }

    // METHODS    
    
    @Override
    public String toString() {
        return "Person{" +
        		"idPerson=" + idPerson + '\'' +
        		"firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName, birthDate, phoneNumber);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(firstName, person.firstName) &&
               Objects.equals(lastName, person.lastName) &&
               Objects.equals(birthDate, person.birthDate) &&
               Objects.equals(phoneNumber, person.phoneNumber);
    }
}
