package com.ordemDeServico.Repository;

import com.ordemDeServico.model.OrdemServico;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Integer> {

    List<OrdemServico> findAllById(int idDoUser);
    List<OrdemServico> findAllByCriadorId(Long executorId);
    @Query("SELECT os FROM OrdemServico os WHERE os.executor.id = :idExecutor OR (os.status = 'ABERTO' AND os.executor IS NULL)")
    List<OrdemServico> findParaExecutor(@Param("idExecutor") Integer idExecutor);
}
