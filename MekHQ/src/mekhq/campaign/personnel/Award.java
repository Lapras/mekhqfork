/*
 * Copyright (c) 2018-2022 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * MekHQ is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MekHQ. If not, see <http://www.gnu.org/licenses/>.
 */
package mekhq.campaign.personnel;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import mekhq.MekHQ;
import mekhq.utilities.MHQXMLUtility;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents an award given to a person
 * @author Miguel Azevedo
 */
@XmlRootElement(name = "award")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class Award implements Comparable<Award> {
    @XmlElement(name = "name")
    private String name;

    @XmlElement(name = "description")
    private String description;

    @XmlElement(name = "group")
    private String group = "null";

    @XmlElement(name = "medal")
    private List<String> medals;

    @XmlElement(name = "ribbon")
    private List<String> ribbons;

    @XmlElement(name = "misc")
    private List<String> miscs;

    @XmlElement(name = "xp")
    private int xp = 0;

    @XmlElement(name = "edge")
    private int edge = 0;

    @XmlElement(name = "qty")
    private int qty;

    @XmlElement(name = "item")
    private String item;

    @XmlElement(name = "size")
    private String size;

    @XmlElement(name = "range")
    private String range;

    @XmlElement(name = "stackable")
    private boolean stackable = false;

    private int id;

    private String set;

    private List<LocalDate> dates;

    public Award() {

    }

    public Award(String name, String set,  String description, String group, List<String> medals, List<String> ribbons,
                 List<String> miscs, int xp, int edge, boolean stackable, int qty, String item, String size, String range, int id) {
        this.name = name;
        this.set = set;
        this.description = description;
        this.group = group;
        this.medals = medals;
        this.ribbons = ribbons;
        this.miscs = miscs;
        this.xp = xp;
        this.edge = edge;
        this.qty = qty;
        this.item = item;
        this.size = size;
        this.range = range;
        this.stackable = stackable;
        dates = new ArrayList<>();
        this.id = id;
    }

    /**
     * Writes this award to xml file and format.
     * @param pw printer writer reference to write the xml
     * @param indent indentation
     */
    public void writeToXML(final PrintWriter pw, int indent) {
        MHQXMLUtility.writeSimpleXMLOpenTag(pw, indent++, "award");
        for (LocalDate date : dates) {
            MHQXMLUtility.writeSimpleXMLTag(pw, indent, "date", date);
        }
        MHQXMLUtility.writeSimpleXMLTag(pw, indent, "set", set);
        MHQXMLUtility.writeSimpleXMLTag(pw, indent, "name", name);
        MHQXMLUtility.writeSimpleXMLCloseTag(pw, --indent, "award");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSet() {
        return set;
    }

    public void setSet(String set) {
        this.set = set;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(final String group) {
        this.group = group;
    }

    /**
     * Returns the xml element 'qty'
     * Use getQuantity() if looking for the number of times an award has been issued to an individual
     */
    public int getQty() {
        return qty;
    }

    public String getItem() {
        return item;
    }

    public String getSize() {
        return size;
    }

    public String getRange() {
        return range;
    }

    public Boolean isStackable() {
        return stackable;
    }

    /**
     * Gets the file name of an award given i times.
     * @param i times given the award
     * @param fileNames list containing all of the file names
     * @return the file name
     */
    private String getFileName(int i, List<String> fileNames) {
        if (i > fileNames.size()) {
            return fileNames.get(fileNames.size()-1);
        }

        return fileNames.get(i-1);
    }

    /**
     * @param i number of times this award has been awarded
     * @return the filename of the ribbon
     */
    public String getRibbonFileName(int i) {
        return getFileName(i, ribbons);
    }

    public int getNumberOfRibbonFiles() {
        return ribbons == null ? 0 : ribbons.size();
    }

    /**
     * @param i number of times this award has been awarded
     * @return the filename of the medal
     */
    public String getMedalFileName(int i) {
        return getFileName(i, medals);
    }

    public int getNumberOfMedalFiles() {
        return medals == null ? 0 : medals.size();
    }

    /**
     * @param i number of times this award has been awarded
     * @return the filename of the misc
     */
    public String getMiscFileName(int i) {
        return getFileName(i, miscs);
    }

    public int getNumberOfMiscFiles() {
        return miscs == null ? 0 : miscs.size();
    }

    public int getXPReward() {
        return xp;
    }

    public int getEdgeReward() {
        return edge;
    }

    /**
     * Creates a copy of this award and sets a given date.
     * @return award with new date
     */
    public Award createCopy() {
        return new Award(this.name, this.set, this.description, this.medals, this.ribbons, this.miscs,
                this.xp, this.edge, this.stackable, this.qty, this.item, this.size, this.range, this.id);
    }

    /**
     * Checks if an award can be awarded to a given person
     * @param person to be given the award
     * @return true if this award can be awarded to the selected person
     */
    public boolean canBeAwarded(Person person) {
        // If we wish to force the user to not be able to give awards for some reason (e.g. lack of kill count),
        // we need to create classes for each awards and override this method.
        return (!person.getAwardController().hasAward(this) || stackable);
    }

    /**
     * Checks if an award can be awarded to a given group of people
     * @param people to be given the award
     * @return true if this award can be awarded to the selected people
     */
    public boolean canBeAwarded(Person... people) {
        for (Person person : people) {
            if (!canBeAwarded(person)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if two awards are equal
     * @param set is the name of the set of this award
     * @param name is the name of the award
     * @return true if it is equal
     */
    public boolean equals(String set, String name) {
        return (this.set.equals(set) && this.name.equals(name));
    }

    /**
     * Compares an award with this one by priority: xp, edge and name. Used for sorting.
     * @param other award to be compared
     * @return int used for sorting
     */
    @Override
    public int compareTo(Award other) {
        return Integer.compare(this.id, other.id);
    }

    /**
     * Adds a date to the award, as if the award was given again.
     * @param date to be added.
     */
    public void addDate(LocalDate date) {
        dates.add(date);
    }

    public void setDates(List<LocalDate> dates) {
        this.dates = dates;
    }

    /**
     * Merges all dates from one award into the other
     * @param award from the dates will be collected
     */
    public void mergeDatesFrom(Award award) {
        this.dates.addAll(award.dates);
    }

    /**
     * Generates a list of strings of formatted dates
     * @return a list of strings representing the dates in the input format
     */
    public List<String> getFormattedDates() {
        List<String> formattedDates = new ArrayList<>();
        for (LocalDate date : dates) {
            formattedDates.add(MekHQ.getMHQOptions().getDisplayFormattedDate(date));
        }
        return formattedDates;
    }

    /**
     * @param date date to be removed from this award
     */
    public void removeDate(LocalDate date) {
        dates.remove(date);
    }

    /**
     * @return true if this award has multiple (more than 1) dates
     */
    public boolean hasDates() {
        return dates.size() > 1;
    }

    /**
     * @return the number of times this award has been awarded.
     */
    public int getQuantity() {
        return dates.size();
    }

    /**
     * @return an html formatted string to be used as tooltip.
     */
    public String getTooltip() {
        StringBuilder string = new StringBuilder();
        string.append("<html>").append(getName()).append("<br>").append(getDescription())
                .append("<br>").append("<br>");
        for (String date : getFormattedDates()) {
            string.append("(").append(date).append(")").append("<br>");
        }

        string.append("</html>");

        return string.toString();
    }

    /**
     * Replaces a date of an award
     * @param toReplace date to replace
     * @param newDate new date that will replace the other
     */
    public void replaceDate(LocalDate toReplace, LocalDate newDate) {
        if (toReplace.equals(newDate)) {
            return;
        }

        int i = 0;
        for (LocalDate date : dates) {
            if (date.equals(toReplace)) {
                continue;
            }
            i++;
        }
        dates.set(i, newDate);
    }
}
