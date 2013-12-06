package br.com.rehuza.contab.service;

import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;

import br.com.rehuza.contab.data.Repositorio;
import br.com.rehuza.contab.model.Cliente;

@ManagedBean
public class ClienteMB {

	@EJB
	private Repositorio repositorio;
	private Cliente cliente = new Cliente();
	private List<Cliente> clientes;
	private Cliente clienteSelecionado;
	private List<Cliente> clientesFiltrados;

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<Cliente> getClientes() {
		if (this.clientes == null) {
			this.clientes = this.repositorio.lista();
		}
		return this.clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public Cliente getClienteSelecionado() {
		return clienteSelecionado;
	}

	public void setClienteSelecionado(Cliente clienteSelecionado) {
		this.clienteSelecionado = clienteSelecionado;
	}

	public List<Cliente> getClientesFiltrados() {
		return this.clientesFiltrados;
	}

	public void setClientesFiltrados(List<Cliente> clientesFiltrados) {
		this.clientesFiltrados = clientesFiltrados;
	}

	public void adiciona() {
		this.repositorio.adiciona(this.cliente);
		this.cliente = new Cliente();
		this.cliente = null;
		this.lista();
	}

	public List<Cliente> lista() {

		if (this.clientesFiltrados == null) {
			this.clientesFiltrados = this.repositorio.lista();
		}
		return this.clientesFiltrados;
	}
}
