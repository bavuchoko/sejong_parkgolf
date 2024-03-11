package com.pjs.golf.game.service.impl;

import com.pjs.golf.account.entity.Account;
import com.pjs.golf.account.entity.AccountRole;
import com.pjs.golf.common.exception.PermissionLimitedCustomException;
import com.pjs.golf.game.entity.Game;
import com.pjs.golf.game.entity.Score;
import com.pjs.golf.game.service.ScoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ScroeServiceImpl implements ScoreService {

    private final GameServiceImpl gameService;

    @Override
    public List getScoreList(int id) {

        return null;
    }


}
