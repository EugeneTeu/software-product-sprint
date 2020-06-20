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
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import java.io.IOException;
import java.util.List;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.PrintWriter;
import com.google.sps.service.QueryHandler;
import com.google.sps.service.ImageHandler;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns a list of comment. */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  private final Gson gson = new Gson();
  private final QueryHandler queryHandler = new QueryHandler(DatastoreServiceFactory.getDatastoreService());
  private final BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
  private final ImageHandler imageHandler = new ImageHandler();

  /** Converts an list of Comment into a JSON string using the Gson library. */
  private String convertToJsonUsingGson(List<Comment> list) {
    String json = gson.toJson(list);
    return json;
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    List<Comment> comments = queryHandler.getAllComments();
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
    
    queryHandler.addComment(text);
    
    // Get the URL of the image that the user uploaded to Blobstore.
    String imageUrl = imageHandler.getUploadedFileUrl(request, "image");
    
    //TODO: serve Blob
    // Output some HTML that shows the data the user entered.
    PrintWriter out = response.getWriter();
    out.println("<p>Here's the image you uploaded:</p>");
    out.println("<a href=\"" + imageUrl + "\">");
    out.println("<img src=\"" + imageUrl + "\" />");
    out.println("</a>");
    out.println("<p>Here's the comment you entered:</p>");
    out.println(text);
  }

}
