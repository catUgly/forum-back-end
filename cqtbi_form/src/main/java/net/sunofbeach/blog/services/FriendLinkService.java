package net.sunofbeach.blog.services;

import net.sunofbeach.blog.pojo.FriendLink;
import net.sunofbeach.blog.response.Result;

public interface FriendLinkService {

    Result addFriendLink(FriendLink friendLink);

    Result deleteFriendLink(String friendLinkId);

    Result updateFriendLink(String friendLinkId, FriendLink friendLink);

    Result friendLink();

    Result getFriendLink(String friendLinkId);
}
