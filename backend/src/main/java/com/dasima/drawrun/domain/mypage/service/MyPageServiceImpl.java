package com.dasima.drawrun.domain.mypage.service;

import com.dasima.drawrun.domain.mypage.dto.response.BookMarkResponse;
import com.dasima.drawrun.domain.mypage.dto.response.ShowInfoResponse;
import com.dasima.drawrun.domain.mypage.entity.BookMark;
import com.dasima.drawrun.domain.mypage.mapper.MyPageMapper;
import com.dasima.drawrun.domain.user.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyPageServiceImpl implements MyPageService{
    @Autowired
    MyPageMapper myPageMapper;

    public ShowInfoResponse showinfo(int userId){
        return myPageMapper.showinfo(userId).toShowInfoResponseDto();
    }

    public List<BookMarkResponse> bookmark(int userId){
        List<BookMark> bookMarks = myPageMapper.bookmark(userId);
        return bookMarks.stream().map(BookMark::toBookMarkEntity).collect(Collectors.toList());
    }
}
