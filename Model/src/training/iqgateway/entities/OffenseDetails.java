package training.iqgateway.entities;

import java.io.Serializable;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

@Entity
@NamedQueries({
  @NamedQuery(name = "OffenseDetails.findAll", query = "select o from OffenseDetails o")
})
@Table(name = "OFFENSE_DETAILS")
public class OffenseDetails implements Serializable {
    @Id
    @Column(name="OFFENCE_ID", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "OffenseDetailsSeq")
    @SequenceGenerator(name = "OffenseDetailsSeq", sequenceName = "OFFENCE_DETAIL_SEQ")
    private Long offenceId;
    @Column(name="OFFENCE_TYPE", nullable = false, length = 20)
    private String offenceType;
    @Column(nullable = false)
    private Long penalty;
    @Column(name="VEH_TYPE", nullable = false, length = 20)
    private String vehType;
    @OneToMany(mappedBy = "offenseDetails")
    private List<Offense> offenseList;

    public OffenseDetails() {
    }

    public OffenseDetails(Long offenceId, String offenceType, Long penalty,
                          String vehType) {
        this.offenceId = offenceId;
        this.offenceType = offenceType;
        this.penalty = penalty;
        this.vehType = vehType;
    }

    public Long getOffenceId() {
        return offenceId;
    }

    public void setOffenceId(Long offenceId) {
        this.offenceId = offenceId;
    }

    public String getOffenceType() {
        return offenceType;
    }

    public void setOffenceType(String offenceType) {
        this.offenceType = offenceType;
    }

    public Long getPenalty() {
        return penalty;
    }

    public void setPenalty(Long penalty) {
        this.penalty = penalty;
    }

    public String getVehType() {
        return vehType;
    }

    public void setVehType(String vehType) {
        this.vehType = vehType;
    }

    public List<Offense> getOffenseList() {
        return offenseList;
    }

    public void setOffenseList(List<Offense> offenseList) {
        this.offenseList = offenseList;
    }

    public Offense addOffense(Offense offense) {
        getOffenseList().add(offense);
        offense.setOffenseDetails(this);
        return offense;
    }

    public Offense removeOffense(Offense offense) {
        getOffenseList().remove(offense);
        offense.setOffenseDetails(null);
        return offense;
    }
}
