package com.google.sps.queries;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import java.util.List;
import com.google.sps.data.Comment;

/** QueryHandler to handle all queries between datastore and api endpoints. */
public class QueryHandler { 

  private final DatastoreService datastore;

  public QueryHandler(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Add a comment to datastore */
  public void addComment(String text) {
    long timestamp = System.currentTimeMillis();
    Entity commentEntity = new Entity(Comment.COMMENT);
    commentEntity.setProperty(Comment.TEXT, text);
    commentEntity.setProperty(Comment.TIMESTAMP, timestamp);
    datastore.put(commentEntity);
  }

  /** Get all comments from datastore */
  public List<Comment> getAllComments() {
    Query query = new Query(Comment.COMMENT).addSort(Comment.TIMESTAMP, SortDirection.DESCENDING);
    List<Comment> comments = new ArrayList();
    PreparedQuery results = this.datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String text = (String) entity.getProperty(Comment.TEXT);
      long timestamp = (long) entity.getProperty(Comment.TIMESTAMP);
      Comment comment = new Comment(id, text, timestamp);
      comments.add(comment);
    }
    return comments;
  }

}
