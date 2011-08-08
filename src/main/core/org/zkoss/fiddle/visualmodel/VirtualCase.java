package org.zkoss.fiddle.visualmodel;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.zkoss.fiddle.model.Case;
import org.zkoss.fiddle.model.Resource;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * a virtual case object used for visual case.
 *
 * @author tony
 *
 */
public class VirtualCase implements Comparable<Object> {

	private Case $case;

	private List<Resource> resources;

	private Date createDate;

	public VirtualCase(){
		createDate = new Date();
	}

	public VirtualCase(Case $case,List<Resource> res){
		this.$case = $case;
		this.resources = res;
		createDate = new Date();
	}

	public Case getCase() {
		return $case;
	}

	public void setCase(Case $case) {
		this.$case = $case;
	}

	public List<Resource> getResources() {
		return resources;
	}

	public void setResources(List<Resource> resources) {
		this.resources = resources;
	}

	public boolean equals(final Object other) {
		if (!(other instanceof VirtualCase))
			return false;
		VirtualCase castOther = (VirtualCase) other;
		return new EqualsBuilder()
			.append($case.getToken(), castOther.$case.getToken())
			.append(resources, castOther.resources).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder().append($case).append(resources).toHashCode();
	}

	public int compareTo(final Object other) {
		VirtualCase castOther = (VirtualCase) other;
		return new CompareToBuilder().append(createDate, castOther.createDate).toComparison();
	}


	public Date getCreateDate() {
		return createDate;
	}

	public String toString() {
			return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE).append("$case", $case)
					.append("resources", resources).append("createDate", createDate).toString();
		}

}
