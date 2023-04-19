package com.workshop.pizza.service;

import java.util.List;
import java.util.Optional;

public interface ICommonService<T> {

	public List<T> getAll();

	public T save(T t);

	public T update(T t, int id);

	public Optional<T> getById(int id);

	public void delete(int id);
}
