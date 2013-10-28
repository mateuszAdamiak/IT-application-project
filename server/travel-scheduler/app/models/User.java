package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import play.db.ebean.Model;

@Entity
public class User extends Model
{
	private static final long serialVersionUID = 1L;
	private static final String USER_AUTHENTICATED = "User authenticated";
	private static final String AUTHORIZING_FAILED = "Authorizing failed";
	private static final String USER_HAS_BEEN_CREATED = "User has been created";
	public static final Finder<Long, User> find = new Finder<Long, User>(Long.class, User.class);

	@Id
	public long id;
	public String login;
	public String password;

	public static Response register(User user)
	{
		if (find.where().eq("login", user.login).findUnique() == null)
		{
			user.save();
			return registrationOk(user);
		}
		return null;
	}

	public static Response authenticate(String login, String passwordHash)
	{
		final User user = find.where().eq("login", login).eq("password", passwordHash).findUnique();
		return user == null ? unauthorized() : authenticationOk(user);
	}

	public static void delete(User user)
	{
		final User found = find.where().eq("login", user.login).findUnique();
		if(found != null) {
			found.delete();
		}
	}

	@Override
	public String toString()
	{
		return String.format("[id: %s, login: %s, password: %s]", id, login, password);
	}

	private static Response registrationOk(User user)
	{
		final Response response = new Response();
		response.code = ResponseCode.OK;
		response.message = USER_HAS_BEEN_CREATED;
		response.data = new ResponseData();
		response.data.userId = user.id;
		response.data.login = user.login;
		return response;
	}

	private static Response authenticationOk(User user)
	{
		final Response response = new Response();
		response.code = ResponseCode.OK;
		response.message = USER_AUTHENTICATED;
		response.data = new ResponseData();
		response.data.userId = user.id;
		response.data.login = user.login;
		return response;
	}

	private static Response unauthorized()
	{
		final Response response = new Response();
		response.code = ResponseCode.UNAUTHORIZED;
		response.message = AUTHORIZING_FAILED;
		response.data = new ResponseData();
		response.data.trialsLeft = 3;
		return response;
	}
}
