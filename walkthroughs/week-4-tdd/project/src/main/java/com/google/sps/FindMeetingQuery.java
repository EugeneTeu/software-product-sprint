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
import java.lang.Math;

public final class FindMeetingQuery {
  public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
    // My Approach: 
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

    // Start of solution 
    Collection<String> attendees = request.getAttendees();

    // option for no attendees
    if (attendees.size() == 0) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }
    Long durationOfMeeting = request.getDuration();
    // check if duration too long;
    if (durationOfMeeting > TimeRange.WHOLE_DAY.duration()) {
      return Arrays.asList();
    }

    Collection<Event> currentAttendeesEvent = getAttendeesEvent(attendees, events);

    if (currentAttendeesEvent.size() == 0) {
      return Arrays.asList(TimeRange.WHOLE_DAY);
    }

    Collection<TimeRange> mergedEventList = mergeTimeRanges(currentAttendeesEvent);

    // we now know where are the free slots in a day
    Collection<TimeRange> possibleFreeSlots = getFreeSlots(mergedEventList, durationOfMeeting);
    
    return possibleFreeSlots;
  }

  // Filters the attendees events required for this meeting request e.g events that will cause a meeting to fail
  public static Collection<Event> getAttendeesEvent(Collection<String> attendees, Collection<Event> events) {
    // stream implementation
    // Had trouble using streams so changed to simple for loop iteration
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

  public static Collection<TimeRange> mergeTimeRanges(Collection<Event> events) {
    List<TimeRange> listTimeRange = new ArrayList();

    for (Event event: events) {
      TimeRange timeRange = event.getWhen();
      listTimeRange.add(timeRange);
    }

    // order the time ranges by starting
    Collections.sort(listTimeRange, TimeRange.ORDER_BY_START);
    List<TimeRange> mergedListTimeRange = new ArrayList();

    // we take first range as current
    // e.g [[1,4],[2,6]]
    // put [1,4] inside result list first
    TimeRange currentTimeRange = listTimeRange.get(0);
    mergedListTimeRange.add(currentTimeRange);
    if (listTimeRange.size() == 1) {
      return mergedListTimeRange;
    }

    for (int i = 1; i < listTimeRange.size(); i++) {
      TimeRange nextTimeRange = listTimeRange.get(i);
      if (currentTimeRange.end() >= nextTimeRange.start()) {
        int biggerEndTime = Math.max(currentTimeRange.end(), nextTimeRange.end());
      
        // we increment the current range e.g [1,4],[2,6] => [1,6]
        // this might not be the optimum interval e.g ... [2,6], [5,7] ...
        // we need to continue checking with this currentTimeRange
        currentTimeRange = TimeRange.fromStartEnd(currentTimeRange.start(), biggerEndTime, false);
        // if we need to change the time range inside the result list
        mergedListTimeRange.remove(mergedListTimeRange.size() - 1);
        mergedListTimeRange.add(currentTimeRange);
      } else {
        // if the next one does not overlap with current, we can take the next one 
        currentTimeRange = listTimeRange.get(i);
        mergedListTimeRange.add(currentTimeRange);
      }
    }

    return mergedListTimeRange;
  }

  public static Collection<TimeRange> getFreeSlots(Collection<TimeRange> mergedEventList, long durationOfMeeting) {
    List<TimeRange> freeSlotsList = new ArrayList();
    List<TimeRange> timeRangeList = new ArrayList(mergedEventList);

    int currentStart = TimeRange.START_OF_DAY;
    // for free slots up to the last time range
    for (int i = 0 ; i < timeRangeList.size(); i++) {
      TimeRange range = timeRangeList.get(i);
      // first interval
      // e.g both start time is same
      if (currentStart < range.start()) {
        // check that the spacing can fit the meeting requested
        if ((range.start() - currentStart) >= durationOfMeeting) {
          freeSlotsList.add(TimeRange.fromStartEnd(currentStart, range.start(), false ));
        }
      }
      currentStart = range.end();
    }
    // after last free slot 
    TimeRange lastTimeRange = timeRangeList.get(timeRangeList.size() - 1);
    if ( (TimeRange.END_OF_DAY - lastTimeRange.end()) >= durationOfMeeting ) {
      freeSlotsList.add(TimeRange.fromStartEnd(lastTimeRange.end(), TimeRange.END_OF_DAY, true));
    }
    return freeSlotsList;
  }
}
