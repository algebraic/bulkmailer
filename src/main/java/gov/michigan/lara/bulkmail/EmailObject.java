package gov.michigan.lara.bulkmail;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class EmailObject implements Serializable {

	private static final long serialVersionUID = 1L;
    private String email;
}