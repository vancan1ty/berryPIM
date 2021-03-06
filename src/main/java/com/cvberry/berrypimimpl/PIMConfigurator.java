package com.cvberry.berrypimimpl;

import com.cvberry.berrypim.Anchor;
import com.cvberry.berrypim.Configurator;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by vancan1ty on 1/4/2016.
 */
public class PIMConfigurator implements Configurator {
    @Override
    public void doConfiguration(String rootPath) throws IOException, URISyntaxException {
        Anchor myAnchor = Anchor.getInstance();
        myAnchor.getDispatcher().addDispatchPath("",new DashboardController("dashboard/"));
        myAnchor.getDispatcher().addDispatchPath("dashboard",new DashboardController("dashboard/"));
        myAnchor.getDispatcher().addDispatchPath("raw",new RawController("raw/"));
        myAnchor.getDispatcher().addDispatchPath("calendar/home",new CalendarController("calendar/home/","calendar/"));
        myAnchor.getDispatcher().addDispatchPath("calendar/effective",new EffectiveCalendarController("calendar/effective"));
        myAnchor.getDispatcher().addDispatchPath("contacts",new ContactsController3("contacts/"));
        myAnchor.getDispatcher().addDispatchPath("notes",new NotesController("notes/"));
        myAnchor.getDispatcher().addDispatchPath("todos",new TodoController("todos/"));
        myAnchor.getDispatcher().addDispatchPath("finance",new FinanceController("finance/"));
        myAnchor.getDispatcher().addDispatchPath("email",new EmailController("email/"));
        myAnchor.getDispatcher().addDispatchPath("graphs",new GraphsController("graphs/"));
        myAnchor.getDispatcher().addDispatchPath("systemInfo",new SystemInfoController("systemInfo/"));
        //myAnchor.getDispatcher().addDispatchPath("contacts",new ContactsController("contacts/"));
        //myAnchor.getDispatcher().addDispatchPath("contacts2",new ContactsController2("contacts2/"));
    }

}
