package Enterprise.misc;

import java.lang.annotation.*;

/**
 * Annotation for {@link Enterprise.data.concurrent.UpdateService}.
 * It tries to update the database with every {@link javafx.beans.property.Property}
 * field marked with this annotation.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SQL {
}
