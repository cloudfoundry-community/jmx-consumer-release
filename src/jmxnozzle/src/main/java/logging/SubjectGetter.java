package logging;

import javax.security.auth.Subject;
import java.security.AccessControlContext;
import java.security.AccessController;

public class SubjectGetter {
    public SubjectGetter() {
    }

    public Subject getSubject() {
        AccessControlContext acc = AccessController.getContext();
        Subject subject = Subject.getSubject(acc);
        return subject;
    }
}
