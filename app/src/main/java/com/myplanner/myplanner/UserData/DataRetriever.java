package com.myplanner.myplanner.UserData;

import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class DataRetriever {
    ArrayList<PlannerEvent> events = new ArrayList<>();
    ArrayList<PlannerNote> notes = new ArrayList<>();
    ArrayList<PlannerReminder> reminders = new ArrayList<>();

    int currentTab = 0;

    final int millsPerHour = 3600000;
    final int millsPerMinute = 60000;

    //----------------------------------------------------------------------------------------------
    //-------------------------------- Singleton Class Declarations --------------------------------
    //----------------------------------------------------------------------------------------------

    private DataRetriever() {}

    private static DataRetriever instance = null;

    public static DataRetriever getInstance() {
        if (instance == null) {
            instance = new DataRetriever();
            return instance;
        } else {
            return instance;
        }
    }

    //----------------------------------------------------------------------------------------------
    //------------------------------------------ Getters -------------------------------------------
    // ---------------------------------------------------------------------------------------------

    public int getCurrentTab() {return currentTab;}

    public PlannerEvent getEvent(int position) {return events.get(position);}

    public PlannerEvent getEventByID(int id) {
        for (int i = 0; i < events.size(); ++i) {
            if (events.get(i).id == id) {
                return events.get(i);
            }
        }
        return null;
    }

    public int getNumEvents() {return events.size();}

    public PlannerNote getNote(int position) {return notes.get(position);}

    public PlannerNote getNoteByID(int id) {
        for (int i = 0; i < notes.size(); ++i) {
            if (notes.get(i).id == id) {
                return notes.get(i);
            }
        }
        return null;
    }

    public int getNumNotes() {return notes.size();}

    public PlannerReminder getReminder(int position) {return reminders.get(position);}

    public PlannerReminder getReminderByID(int id) {
        for (int i = 0; i < reminders.size(); ++i) {
            if (reminders.get(i).id == id) {
                return reminders.get(i);
            }
        }
        return null;
    }

    public int getNumReminders() {return reminders.size();}

    //----------------------------------------------------------------------------------------------
    //-------------------------------------- Filtered Getters --------------------------------------
    // ---------------------------------------------------------------------------------------------

    // gets a list of the events for some date
    public ArrayList<PlannerEvent> getDateEvents(int year, int month, int date) {
        Log.i("DataRetriever", "getting events for date " + date);

        ArrayList<PlannerEvent> dayEvents = new ArrayList<>();
        int pos = 0;
        int newPos = 0;

        if (events == null || events.size() == 0) {
            return dayEvents;
        }

        // look at all events that start before the target day to see if they end after the target
        PlannerEvent curEvent = events.get(0);
        while (year > curEvent.getStartYear() ||
                (year == curEvent.getStartYear() && month > curEvent.getStartMonth()) ||
                (year == curEvent.getStartYear() && month == curEvent.getStartMonth() && date > curEvent.getStartDate())) {

            if (curEvent.getEndYear() == year && curEvent.getEndMonth() == month
                    && curEvent.getEndDate() == date) {
                long midnightInMills = curEvent.getStartMills() - (curEvent.getStartHour() * millsPerHour + curEvent.getStartMinute() * millsPerMinute);
                PlannerEvent tempEvent = new PlannerEvent(midnightInMills, curEvent.getEndMills(), curEvent.getTitle(), curEvent.getMessage(), curEvent.getID());
                dayEvents.add(newPos++, tempEvent);
            } else if (curEvent.getEndYear() >= year && curEvent.getEndMonth() >= month
                    && curEvent.getEndDate() >= date) {
                Log.i("DataRetriever", "adding in event that passes over the whole day, start/end date " + curEvent.getStartDate() + ", " + curEvent.getEndDate());
                long midnightInMills = curEvent.getStartMills() - (curEvent.getStartHour() * millsPerHour + curEvent.getStartMinute() * millsPerMinute);
                long lastMinuteInMills = midnightInMills + 24 * millsPerHour - millsPerMinute;
                PlannerEvent tempEvent = new PlannerEvent(midnightInMills, lastMinuteInMills, curEvent.getTitle(), curEvent.getMessage(), curEvent.getID());
                dayEvents.add(newPos++, tempEvent);
            }

            if (++pos < events.size()) {
                curEvent = events.get(pos);
            } else {
                return dayEvents;
            }
        }

        // look at all events that start at the target date
        while (pos < events.size() && curEvent.getStartYear() == year
                && curEvent.getStartMonth() == month && curEvent.getStartDate() == date) {
            if (curEvent.getEndYear() == year && curEvent.getEndMonth() == month && curEvent.getEndDate() == date) {
                dayEvents.add(newPos++, curEvent);
            } else {
                long lastMinuteInMills = curEvent.getStartMills() + (23 - curEvent.getStartHour()) * millsPerHour + (59 - curEvent.getStartMinute()) * millsPerMinute;
                PlannerEvent tempEvent = new PlannerEvent(curEvent.getStartMills(), lastMinuteInMills, curEvent.getTitle(), curEvent.getMessage(), curEvent.getID());
                dayEvents.add(newPos++, tempEvent);
            }


            if (++pos < events.size()) {
                curEvent = events.get(pos);
            } else {
                return dayEvents;
            }
        }

        return dayEvents;
    }

    // gets a list of the notes for some tag
    public ArrayList<PlannerNote> getTagNotes(int year, ArrayList<String> tags) {
        ArrayList<PlannerNote> tagNotes = new ArrayList<>();
        int newPos = 0;

        for (int i = 0; i < notes.size(); ++i) {
            for (int t1 = 0; t1 < tags.size(); ++t1) {
                for (int t2 = 0; t2 < notes.get(i).getNumTags(); ++t2) {
                    if (notes.get(i).getTag(t2).equals(tags.get(t1))) {
                        tagNotes.add(newPos++, notes.get(i));
                        t1 = tags.size();
                        break;
                    }
                }
            }
        }

        return tagNotes;
    }

    // gets a list of the reminders for some date
    public ArrayList<PlannerReminder> getDateReminders(int year, int month, int date) {
        ArrayList<PlannerReminder> dayReminders = new ArrayList<>();
        int pos = 0;
        int newPos = 0;

        while (pos < reminders.size() && year > reminders.get(pos++).getYear());
        while (pos < reminders.size() && month > reminders.get(pos++).getMonth());
        while (pos < reminders.size() && date > reminders.get(pos++).getDate());
        while (pos < reminders.size() && date == reminders.get(pos).getDate()) {
            dayReminders.add(newPos++, reminders.get(pos++));
        }

        return dayReminders;
    }

    //----------------------------------------------------------------------------------------------
    //------------------------------------- Addition Functions -------------------------------------
    // ---------------------------------------------------------------------------------------------

    public void setCurrentTab(int tab) {currentTab = tab;}

    public void addEvent(PlannerEvent event) {
        Log.i("CreateEvent12dataretri", "Event recieved has id " + event.getID() + " and starts at " + event.getStartDate() + ", " + event.getStartHour() + ", " + event.getStartMinute());
        for (int k = 0; k < events.size(); ++k) {
            PlannerEvent e = events.get(k);
            Log.i("CreateEvent12data pre", "pEvent number " + k + " has id " + e.getID() + " and starts at " + e.getStartDate() + " at hour " + e.getStartHour() + " at minute " + e.getStartMinute());
            Log.i("CreateEvent12data pre", "         and ends at " + e.getEndDate() + " at hour " + e.getEndHour() + " at minute " + e.getEndMinute());
        }

        int insertPos = 0;
        while (insertPos < events.size() &&
                (events.get(insertPos).getStartMills() < event.getStartMills() ||
                        (events.get(insertPos).getStartMills() == event.getStartMills() &&
                                events.get(insertPos).getEndMills() > event.getEndMills()))) {
            ++insertPos;
        }

        // insert the event into the ArrayList. The items must be shifted manually because the add()
        //   function in ArrayList may not preserve order.
        if (events.size() == 0) {
            events.add(event);
        } else {
            events.add(events.size(), event);

            for (int i = events.size() - 1; i > insertPos; --i) {
                events.set(i, events.get(i - 1));
            }

            events.set(insertPos, event);
        }

        for (int x = 0; x < events.size(); ++x) {
            PlannerEvent e = events.get(x);
            Log.i("CreateEvent12data post", "aEvent number " + x + " has id " + e.getID() + " and starts at " + e.getStartDate() + " at hour " + e.getStartHour() + " at minute " + e.getStartMinute());
            Log.i("CreateEvent12data pre", "         and ends at " + e.getEndDate() + " at hour " + e.getEndHour() + " at minute " + e.getEndMinute());
        }
    }

    public void addNote(PlannerNote note) {
        notes.add(note);
    }

    public void addReminder(PlannerReminder reminder) {
        int insertPos = 0;

        while (insertPos + 1 < reminders.size()
                && reminders.get(insertPos).getMills() < reminder.getMills()) {
            ++insertPos;
        }

        // insert the reminder into the ArrayList. The items must be shifted manually because the
        //   add() function in ArrayList may not preserve order.
        if (reminders.size() == 0) {
            reminders.add(reminder);
        } else {
            reminders.add(reminders.size(), reminder);

            for (int i = reminders.size() - 1; i > insertPos; --i) {
                reminders.set(i, reminders.get(i - 1));
            }

            reminders.set(insertPos, reminder);
        }
    }

    //----------------------------------------------------------------------------------------------
    //------------------------------------- Removal Functions --------------------------------------
    // ---------------------------------------------------------------------------------------------

    public void removeEvent(int id) {
        int i = 0;

        while (i < events.size()) {
            if (events.get(i).id == id) {
                events.remove(i);
                return;
            }

            ++i;
        }
    }

    public void removeNote(int id) {
        int i = 0;

        while (i < notes.size()) {
            if (notes.get(i).id == id) {
                notes.remove(i);
                return;
            }

            ++i;
        }
    }

    public void removeReminder(int id) {
        int i = 0;

        while (i < reminders.size()) {
            if (reminders.get(i).id == id) {
                reminders.remove(i);
                return;
            }

            ++i;
        }
    }

    //----------------------------------------------------------------------------------------------
    //------------------------------------ Save/Load Functions -------------------------------------
    // ---------------------------------------------------------------------------------------------


}