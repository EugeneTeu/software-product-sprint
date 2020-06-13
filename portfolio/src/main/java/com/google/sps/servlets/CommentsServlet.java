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

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

import java.io.IOException;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import com.google.sps.queries.QueryHandler;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns a list of comment. */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  private final ArrayList<Comment> comments = new ArrayList<Comment>();
  private final Gson gson = new Gson();
  private final QueryHandler queryHandler = new QueryHandler(DatastoreServiceFactory.getDatastoreService());

  /** Converts an arrayList of Comment into a JSON string using the Gson library. */
  private String convertToJsonUsingGson(ArrayList<Comment> list) {
    String json = gson.toJson(list);
    return json;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    ArrayList<Comment> commentsResult = queryHandler.getAllComments();
    // remove all current elements
    this.comments.clear();

    this.comments.addAll(commentsResult);

    String commentsJson = convertToJsonUsingGson(comments);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(commentsJson);
  }

  /** @return the request parameter, or the default value if the parameter was not specified by the client. */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get the input from the form.
    // TODO: add user input sanitation
    String text = getParameter(request, "user-comment", "");
    if (text.isEmpty()) {
      response.setContentType("text/html");
      response.getWriter().println("Please enter comment");
      return;
    }

    long timestamp = System.currentTimeMillis();
    Comment comment = Comment.getCommentWithoutID(text, timestamp);
    queryHandler.addComment(comment);
    // Redirect back to the Home page.
    response.sendRedirect("/index.html");
  }

}
