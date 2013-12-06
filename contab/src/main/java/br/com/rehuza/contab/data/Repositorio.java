package br.com.rehuza.contab.data;

import java.util.List;

import br.com.rehuza.contab.model.Cliente;

public interface Repositorio {
	
	public void adiciona (Cliente cliente);
	
	public List<Cliente> lista();

	public Cliente buscaUm(Cliente cliente, Long id);

	public List<Cliente> listaFiltrada(Cliente cliente);	

}
