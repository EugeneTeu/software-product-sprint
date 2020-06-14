package com.google.sps.queries;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.key;
import java.util.ArrayList;
import java.util.List;
import com.google.sps.data.Comment;

/** QueryHandler to handle all queries between datastore and api endpoints. */
public class QueryHandler { 

  private final DatastoreService datastore;
  private final KeyFactory commentKeyFactory;

  public QueryHandler(DatastoreService datastore) {
    this.datastore = datastore;
    this.commentKeyFactory = datastore.newKeyFactory().setKind(Comment.COMMENT_KIND)
  }

  /** Add a comment to datastore */
  public void addComment(String text) {
    Comment comment = new comment(datastore.allocateId(commentKeyFactory.newKey(), text, System.currentTimeMillis()));
    datastore.put(commentEntity.toEntity());
  }

  /** Get all comments from datastore */
  public List<Comment> getAllComments() {
    Query query = new Query(Comment.COMMENT_KIND).addSort(Comment.TIMESTAMP_PROPERTY, SortDirection.DESCENDING);
    List<Comment> comments = new ArrayList();
    PreparedQuery results = this.datastore.prepare(query);
    for (Entity entity : results.asIterable()) {
      Comment comment = new Comment(entity);
      comments.add(comment);
    }
    return comments;
  }

}
