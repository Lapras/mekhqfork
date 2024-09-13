/*
 * Copyright (c) 2020-2022 - The MegaMek Team. All Rights Reserved.
 *
 * This file is part of MekHQ.
 *
 * MekHQ is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
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
package mekhq.campaign.parts.equipment;

import megamek.common.Aero;
import megamek.common.EquipmentType;
import megamek.common.Mek;
import mekhq.campaign.Campaign;
import mekhq.campaign.Quartermaster;
import mekhq.campaign.Warehouse;
import mekhq.campaign.parts.AeroHeatSink;
import mekhq.campaign.parts.Part;
import mekhq.campaign.unit.Unit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class MissingHeatSinkTest {
    @BeforeAll
    public static void beforeAll() {
        EquipmentType.initializeTypes();
    }

    /**
     * https://github.com/MegaMek/mekhq/issues/2365
     */
    @Test
    public void missingHeatSinkSelectsCorrectPartDuringRepair() {
        Campaign mockCampaign = mock(Campaign.class);
        Warehouse warehouse = new Warehouse();
        when(mockCampaign.getWarehouse()).thenReturn(warehouse);
        Quartermaster quartermaster = new Quartermaster(mockCampaign);
        when(mockCampaign.getQuartermaster()).thenReturn(quartermaster);
        Unit unit = mock(Unit.class);
        doAnswer(inv -> {
            Part part = inv.getArgument(0);
            part.setUnit(unit);
            return null;
        }).when(unit).addPart(any());
        Mek mek = mock(Mek.class);
        when(mek.getWeight()).thenReturn(65.0);
        when(unit.getEntity()).thenReturn(mek);
        EquipmentType heatSinkType = mock(EquipmentType.class);

        // Create a missing heat sink on a unit
        int equipmentNum = 17;
        MissingHeatSink missingHeatSink = new MissingHeatSink(1, heatSinkType, equipmentNum, false, mockCampaign);
        missingHeatSink.setUnit(unit);
        warehouse.addPart(missingHeatSink);

        // Add an aero heat sink that isn't legit
        AeroHeatSink aeroHeatSink = new AeroHeatSink(1, Aero.HEAT_DOUBLE, false, mockCampaign);
        warehouse.addPart(aeroHeatSink);

        // Add an incorrect heat sink
        EquipmentType otherHeatSinkType = mock(EquipmentType.class);
        HeatSink otherHeatSink = new HeatSink(1, otherHeatSinkType, -1, false, mockCampaign);
        warehouse.addPart(otherHeatSink);

        // Add the correct heat sink
        HeatSink legitHeatSink = new HeatSink(1, heatSinkType, -1, false, mockCampaign);
        warehouse.addPart(legitHeatSink);

        missingHeatSink.fix();

        assertFalse(warehouse.getParts().contains(missingHeatSink));
        assertNull(missingHeatSink.getUnit());
        assertFalse(warehouse.getParts().contains(legitHeatSink));

        ArgumentCaptor<Part> partCaptor = ArgumentCaptor.forClass(Part.class);
        verify(unit, times(1)).addPart(partCaptor.capture());
        verify(unit, times(1)).removePart(eq(missingHeatSink));

        Part addedPart = partCaptor.getValue();
        assertInstanceOf(HeatSink.class, addedPart);

        HeatSink addedHeatSink = (HeatSink) addedPart;
        assertEquals(unit, addedHeatSink.getUnit());
        assertEquals(equipmentNum, addedHeatSink.getEquipmentNum());
        assertEquals(heatSinkType, addedHeatSink.getType());
    }
}
