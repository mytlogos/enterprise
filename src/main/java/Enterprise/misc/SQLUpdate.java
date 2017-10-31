package Enterprise.misc;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for {@link Enterprise.data.concurrent.UpdateService}.
 * It tries to update the database with every {@link javafx.beans.property.Property}
 * field marked with this annotation.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQLUpdate {

    /**
     * The name of the Field, which has the name of the database column as it´s value.
     * Field needs to be located in the class specified
     * by the {@link DataAccess} annotation of the class.
     *
     * @return String name of the Field
     */
    String columnField();
}
