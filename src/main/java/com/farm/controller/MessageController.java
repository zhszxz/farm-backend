package com.farm.controller;

import com.farm.common.PageResult;
import com.farm.common.Result;
import com.farm.dto.ReplyRequest;
import com.farm.entity.Message;
import com.farm.service.MessageService;
import com.farm.vo.MyMessageVO;
import com.farm.vo.ReceivedMessageVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 留言咨询接口（需登录）
 */
@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public Result<Message> create(@RequestBody Message message) {
        return Result.ok(messageService.create(message.getShopId(), message.getContent()));
    }

    @GetMapping("/sent")
    public Result<PageResult<MyMessageVO>> sent(@RequestParam(defaultValue = "1") long page,
                                                @RequestParam(defaultValue = "10") long size) {
        return Result.ok(messageService.sent(page, size));
    }

    @GetMapping("/received")
    public Result<PageResult<ReceivedMessageVO>> received(@RequestParam(defaultValue = "1") long page,
                                                          @RequestParam(defaultValue = "10") long size) {
        return Result.ok(messageService.received(page, size));
    }

    @PostMapping("/{id}/reply")
    public Result<Message> reply(@PathVariable Long id, @RequestBody ReplyRequest body) {
        return Result.ok(messageService.reply(id, body.getReply()));
    }
}
