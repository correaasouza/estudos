package br.com.rehuza.contab.data;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import br.com.rehuza.contab.model.Cliente;
import br.com.rehuza.reflection.ReflectionUtils;

@Stateless
@Local(Repositorio.class)
public class ClienteRepositorioBean implements Repositorio{

	@PersistenceContext 
	private EntityManager entityManeger; 
	
	@Inject
	private Logger log;
	
	public void adiciona(Cliente cliente) {
		this.entityManeger.persist(cliente);
		
	}

	public List<Cliente> lista() {
		
		CriteriaBuilder qb = this.entityManeger.getCriteriaBuilder();
		CriteriaQuery<Cliente> cq = (CriteriaQuery<Cliente>) qb.createQuery(Cliente.class);
		cq.from(Cliente.class);
		return this.entityManeger.createQuery(cq).getResultList();
		
	}

	@Override
	public Cliente buscaUm(Cliente cliente, Long id) {
		return (Cliente) this.entityManeger.find(Cliente.class, id);
	}

	@Override
	public List<Cliente> listaFiltrada(Cliente cli) {
		TypedQuery<Cliente> query = null;
		try {
			query = (TypedQuery<Cliente>) ReflectionUtils.createQuery(this.entityManeger, cli);
		} catch (Exception e) {
			log.log(Level.SEVERE,e.getMessage(),e);
		}
		return query.getResultList();
	}

}
