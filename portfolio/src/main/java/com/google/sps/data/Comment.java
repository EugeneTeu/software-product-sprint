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
import com.google.appengine.api.datastore.Key;

/** A comment on the portfolio site */
public final class Comment {

  private final Key commentKey;
  private final String text;
  private final long timestamp;

  public static final String COMMENT_KIND = "Comment";
  public static final String TEXT_PROPERTY = "text";
  public static final String TIMESTAMP_PROPERTY = "timestamp";

  public Comment(Key commentKey, String text, long timestamp) {
    this.commentKey = commentKey;
    this.text = text;
    this.timestamp = timestamp;
  }

  public Comment(Entity entity) {
    this(entity.getKey(), (String) entity.getProperty(Comment.TEXT_PROPERTY), (long) entity.getProperty(Comment.TIMESTAMP_PROPERTY));
  }


  /** Takes in string text to convert into Entity */
  public static Entity toEntity(String text) {
    Entity commentEntity = new Entity(Comment.COMMENT_KIND);
    commentEntity.setProperty(TEXT_PROPERTY, text);
    commentEntity.setProperty(TIMESTAMP_PROPERTY, System.currentTimeMillis());
    return commentEntity;
  }    /** Convert current Comment instance into Entity */
  public Entity toEntity() {
    Entity commentEntity = new Entity(COMMENT_KIND);
    commentEntity.setProperty(TEXT_PROPERTY, this.text);
    commentEntity.setProperty(TIMESTAMP_PROPERTY, this.timestamp);
    return commentEntity;
  }

}
