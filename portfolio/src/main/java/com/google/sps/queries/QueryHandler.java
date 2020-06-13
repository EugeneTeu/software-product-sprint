package com.google.sps.queries;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import java.util.ArrayList;
import com.google.sps.data.Comment;

/** QueryHandler to handle all queries between datastore and api endpoints. */
public class QueryHandler { 

  private final DatastoreService datastore;

  public QueryHandler(DatastoreService datastore) {
    this.datastore = datastore;
  }

  /** Add a comment to datastore */
  public void addComment(Comment comment) {
    datastore.put(comment.toEntity());
  }

  /** Get all comments from datastore */
  public ArrayList<Comment> getAllComments() {
    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    ArrayList<Comment> comments = new ArrayList();
    PreparedQuery results = this.datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String text = (String) entity.getProperty("text");
      long timestamp = (long) entity.getProperty("timestamp");
      Comment comment = new Comment(id, text, timestamp);
      comments.add(comment);
    }
    return comments;
  }

}
