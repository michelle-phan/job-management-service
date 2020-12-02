package com.taiger.search.service;

import java.util.UUID;

public interface AirflowService {

  void trigger(UUID uuid);

  void pause(UUID uuid, boolean isPause);
}
