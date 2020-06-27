// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.Collection;
import java.util.Collections;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    //throw new UnsupportedOperationException("TODO: Implement this method.");
    Collection<String> attendees = request.getAttendees();

    // option for no attendees
    if (attendees.size() == 0) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    Collection<Event> currentAttendeesEvent = getAttendeesEvent(attendees, events);
    Collection<Event> mergedEventList = mergeIntervals(currentAttendeesEvent);
    Long durationOfMeeting = request.getDuration();
    // for each attendee, 
    //    find the meeting they currently have 
    // rank the attendee by the number of meetings they have, in descending order
    // Now i have all meeting times of each attendee, my aim is to find a free period between all of them that is >= durationOfMeeting
    // brute force: check one by one 
    // my solution: 
    //    we "traverse" starting from 9 am (start time) to 9 pm 
    //    At each interval of the meeting, we check down the list for start times

    // faster solution 
    // merge all intervals for attendees (reducing the problem) 
    // once we have this list of merge intervals, we just need to check for available times using this duration
    throw new UnsupportedOperationException("TODO: Implement this method.");
  }

  // Filters the attendees events required for this meeting request e.g events that will cause a meeting to fail
  public static Collection<Event> getAttendeesEvent(Collection<String> attendees, Collection<Event> events) {
    // stream implementation
    /*
    Stream<Event> streamEvents = events.stream();
    Stream<String> streamAttendees = attendees.stream();
    // Filter events where the set of attendee whose dont match any of the current attendees
    streamEvents.filter((event) -> !(streamAttendees.anyMatch((attendeeName) -> event.getAttendees().contains(attendeeName))));
    Collection<Event> listEvents = streamEvents.collect(Collectors.toList());
    return listEvents;*/
    
    //String[] attendeesArr = attendees.toArray(new String[0]);

    Event[] eventsArr = events.toArray(new Event[0]);
    List<Event> eventsList = new ArrayList();

    // iterate through events
    for (Event event : eventsArr) {
      Set<String> eventAttendees = event.getAttendees();

      for (String attendeeName: attendees) {
        // if this event belongs to one of the attendees in current request, keep
        if (eventAttendees.contains(attendeeName)) {
          eventsList.add(event);
          // break for this inner for loop, can move on to the next event
          break; 
        }
      }
    }
    // sort based on start time
    return eventsList;
  }

  public static Collection<Event> mergeIntervals(Collection<Event> events) {
    List<TimeRange> listTimeRange = new ArrayList();
    for (Event event: events) {
      TimeRange timeRange = event.getWhen();
      listTimeRange.add(timeRange);
    }
    // order the time ranges by starting
    Collections.sort(listTimeRange, TimeRange.ORDER_BY_START);

    for (TimeRange tr : listTimeRange) {
      System.out.println(tr);
    }

    return new ArrayList();
  }
}
