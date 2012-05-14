import org.junit.*;
import java.util.*;
import play.test.*;
import models.*;

public class BasicTest extends UnitTest {

	@Before
	public void setup() {

		Fixtures.deleteDatabase();

	}

	@Test
	public void createAndRetrieveUser() {

		// Create a new User and save it
		new User("foo@goo.moo", "secret", "foo").save();

		// Retrieve the user with e-mail address foo@goo.moo
		User foo = User.find("byEmail", "foo@goo.moo").first();

		// Test
		assertNotNull(foo);
		assertEquals("foo", foo.fullname);

	}

	@Test
	public void tryConnectAsUser() {

		// Create a new user and save it
		new User("foo@goo.moo", "secret", "foo").save();

		// Test
		assertNotNull(User.connect("foo@goo.moo", "secret"));
		assertNull(User.connect("foo@goo.moo", "badpassword"));
		assertNull(User.connect("bademail@foo.goo", "secret"));

	}

	@Test
	public void createPost() {

		// Create a new User and save it
		User foo = new User("foo@goo.moo", "secret", "foo").save();

		// Create a new Post and save it
		new Post(
				foo,
				"My first Post",
				"Loram ipsum agas sdhooo fu awuwu akuffff snssi dmuro ddmaakg goeri tuitaaamdsu lul.")
				.save();

		// Test that the Post has been created
		assertEquals(1, Post.count());

		// Retrieve all Posts created by foo
		List<Post> fooPosts = Post.find("byAuthor", foo).fetch();

		// Tests
		assertEquals(1, fooPosts.size());
		Post firstPost = fooPosts.get(0);
		assertNotNull(firstPost);
		assertEquals(foo, firstPost.author);
		assertEquals("My first Post", firstPost.title);
		assertEquals(
				"Loram ipsum agas sdhooo fu awuwu akuffff snssi dmuro ddmaakg goeri tuitaaamdsu lul.",
				firstPost.content);
		assertNotNull(firstPost.postedAt);

	}

	@Test
	public void postComments() {

		// Create a new User and save it
		User foo = new User("foo@goo.moo", "secret", "foo").save();

		// Create a new Post and save it
		Post fooPost = new Post(
				foo,
				"My first Post",
				"Loram ipsum agas sdhooo fu awuwu akuffff snssi dmuro ddmaakg goeri tuitaaamdsu lul.")
				.save();

		// Post a first comment
		new Comment(fooPost, "Jeff", "Nice Post").save();
		new Comment(fooPost, "Tom", "Yes i knew that!").save();

		// Retrieve all comments
		List<Comment> fooPostComments = Comment.find("byPost", fooPost).fetch();

		// Tests
		assertEquals(2, fooPostComments.size());

		Comment firstComment = fooPostComments.get(0);
		assertNotNull(firstComment);
		assertEquals("Jeff", firstComment.author);
		assertEquals("Nice Post", firstComment.content);
		assertNotNull(firstComment.postedAt);

		Comment secondComment = fooPostComments.get(1);
		assertNotNull(secondComment);
		assertEquals("Tom", secondComment.author);
		assertEquals("Yes i knew that!", secondComment.content);
		assertNotNull(secondComment.postedAt);

	}

	@Test
	public void useTheCommentsRealtion() {

		// Create a new User and save it
		User foo = new User("foo@goo.moo", "secret", "foo").save();

		// Create a new Post and save it
		Post fooPost = new Post(
				foo,
				"My first Post",
				"Loram ipsum agas sdhooo fu awuwu akuffff snssi dmuro ddmaakg goeri tuitaaamdsu lul.")
				.save();

		// Post a first comment
		assertNotNull(fooPost);
		fooPost.addComment("Jeff", "Nice Post");
		fooPost.addComment("Tom", "Yes i knew that!");

		// Count Things

		assertEquals(1, User.count());
		assertEquals(1, Post.count());
		assertEquals(2, Comment.count());

		// Retrieve foo's post
		fooPost = Post.find("byAuthor", foo).first();
		assertNotNull(fooPost);

		// Navigate to comments
		assertEquals(2, fooPost.comments.size());
		assertEquals("Jeff", fooPost.comments.get(0).author);

		// Delete the post
		fooPost.delete();

		// Check that all comments have been deleted
		assertEquals(1, User.count());
		assertEquals(0, Post.count());
		assertEquals(0, Comment.count());

	}

	@Test
	public void fullTest() {

		Fixtures.loadModels("data.yml");

	    // Count things
	    assertEquals(2, User.count());
	    assertEquals(3, Post.count());
	    assertEquals(3, Comment.count());
	 
	    // Try to connect as users
	    assertNotNull(User.connect("bob@gmail.com", "secret"));
	    assertNotNull(User.connect("jeff@gmail.com", "secret"));
	    assertNull(User.connect("jeff@gmail.com", "badpassword"));
	    assertNull(User.connect("tom@gmail.com", "secret"));
	 
	    // Find all of Bob's posts
	    List<Post> bobPosts = Post.find("author.email", "bob@gmail.com").fetch();
	    assertEquals(2, bobPosts.size());
	 
	    // Find all comments related to Bob's posts
	    List<Comment> bobComments = Comment.find("post.author.email", "bob@gmail.com").fetch();
	    assertEquals(3, bobComments.size());
	 
	    // Find the most recent post
	    Post frontPost = Post.find("order by postedAt desc").first();
	    assertNotNull(frontPost);
	    assertEquals("About the model layer", frontPost.title);
	 
	    // Check that this post has two comments
	    assertEquals(2, frontPost.comments.size());
	 
	    // Post a new comment
	    frontPost.addComment("Jim", "Hello guys");
	    assertEquals(3, frontPost.comments.size());
	    assertEquals(4, Comment.count());

	}
}
