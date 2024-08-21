package com.example.runningservice.repository.chat;

import com.example.runningservice.entity.CrewEntity;
import com.example.runningservice.entity.MemberEntity;
import com.example.runningservice.entity.chat.ChatJoinEntity;
import com.example.runningservice.entity.chat.ChatRoomEntity;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatJoinRepository extends JpaRepository<ChatJoinEntity, Long> {
    // 중복참여 확인
    boolean existsByChatRoomAndMember(ChatRoomEntity chatRoom, MemberEntity member);

    // 특정 채팅방의 멤버 확인
    ChatJoinEntity findByChatRoomAndMember(ChatRoomEntity chatRoom, MemberEntity member);

    // 멤버가 참여중인 크루 chatRoom 확인
    List<ChatJoinEntity> findByMemberAndChatRoom_Crew(MemberEntity member, CrewEntity crew);

    // 참여중인 멤버 수
    int countByChatRoom(ChatRoomEntity chatRoom);

    @Query("SELECT m.nickName FROM ChatJoinEntity c JOIN c.member m WHERE c.chatRoom = :chatRoom")
    List<MemberEntity> findMemberNicknamesByChatRoom(@Param("chatRoom") ChatRoomEntity chatRoom);
}