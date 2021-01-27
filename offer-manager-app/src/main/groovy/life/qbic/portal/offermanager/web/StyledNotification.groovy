package life.qbic.portal.offermanager.web

import com.vaadin.shared.Position
import com.vaadin.ui.Notification

class StyledNotification extends Notification {
    StyledNotification(String caption) {
        super(caption)
        setNotifProperties()
    }

    StyledNotification(String caption, Type type) {
        super(caption, type)
        setNotifProperties()
    }

    StyledNotification(String caption, String description) {
        super(caption, description)
        setNotifProperties()
    }

    StyledNotification(String caption, String description, Type type) {
        super(caption, description, type)
        setNotifProperties()
    }

    private setNotifProperties() {
        this.setPosition(Position.TOP_CENTER)
        this.setDelayMsec(20000)
    }
}
