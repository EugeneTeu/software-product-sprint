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

package com.google.sps.data;

import com.google.appengine.api.datastore.Entity;

/** A comment on the portfolio site */
public final class Comment {

  private final long id;
  private final String text;
  private final long timestamp;

  public static final String TEXT = "text";
  public static final String COMMENT = "Comment";
  public static final String TIMESTAMP = "timestamp";

  public Comment(long id, String text, long timestamp) {
    this.id = id;
    this.text = text;
    this.timestamp = timestamp;
  }

  /** Creates a comment without an ID */
  public static Comment getCommentWithoutID(String text, long timestamp) {
    return new Comment(-1, text, timestamp);
  }

  /** Convert current Comment instance into Entity */
  public Entity toEntity() {
    Entity commentEntity = new Entity(this.COMMENT);
    commentEntity.setProperty(this.TEXT, this.text);
    commentEntity.setProperty(this.TIMESTAMP, this.timestamp);
    return commentEntity;
  }
}
