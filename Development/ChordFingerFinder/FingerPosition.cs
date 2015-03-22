using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    class CFingerPosition
    {

        public EString String { get; private set; }
        public int Fret { get; private set; }

        public CFingerPosition(CFingerPosition other)
        {
            String = other.String;
            Fret = other.Fret;
        }

        bool IsBefore(CFingerPosition other)
        {
            if (String == other.String)
            {
                return Fret < other.Fret;
            }

            if (String < other.String)
            {
                int nFretToCheck =
                    other.Fret + (other.String - String) * Globals.NUMBER_OF_FRETS_IN_OCTAVE;

                return Fret < nFretToCheck;
            }
            else
            {
                int nFretToCheck =
                    Fret + (String - other.String) * Globals.NUMBER_OF_FRETS_IN_OCTAVE;

                return nFretToCheck < other.Fret;
            }
        }

        public bool IsValid()
        {
            if (!IsFretValid(Fret))
            {
                return false;
            }

            if (String < EString.FirstString ||
                String > EString.LastString)
            {
                return false;
            }

            return true;
        }

        bool IsFretValid(int nFret)
        {
            if (nFret >= Globals.NUMBER_OF_FRETS || nFret < 0)
            {
                return false;
            }

            return true;
        }

        public bool PreviousString()
        {
            if (String == EString.FirstString)
            {
                return false;
            }

            int nFret = Fret + SameNoteFretDifferenceOnString(String, String - 1);

            if (!IsFretValid(nFret))
            {
                return false;
            }

            Fret = nFret;
            String--;
            return true;
        }

        public bool NextString()
        {
            if (String == EString.LastString)
            {
                return false;
            }

            int nFret = Fret + SameNoteFretDifferenceOnString(String, String + 1);

            if (!IsFretValid(nFret))
            {
                return false;
            }
            String++;
            Fret = nFret;

            return true;
        }

        public bool NextOctave()
        {
            return AdvanceFrets(Globals.NUMBER_OF_FRETS_IN_OCTAVE);
        }

        public bool PreviousOctave()
        {
            return AdvanceFrets(-1 * Globals.NUMBER_OF_FRETS_IN_OCTAVE);
        }

        public bool AdvanceFrets(int nNumberOfFrets)
        {
            if (nNumberOfFrets == 0)
            {
                return true;
            }

            if (IsLastPosition())
            {
                return PreviousOctave();
            }

            int nNumberOfFretsToAdvance = nNumberOfFrets;

            while (nNumberOfFretsToAdvance != 0)
            {
                int nSameNotesDifference = 0;

                if (nNumberOfFretsToAdvance < 0)
                {
                    nSameNotesDifference = SameNoteFretDifferenceOnString(String, String - 1);
                }
                else
                {
                    nSameNotesDifference = SameNoteFretDifferenceOnString(String, String + 1);
                }

                int nAbsSameNotesDifference = Math.Abs(nSameNotesDifference);

                if (Math.Abs(nNumberOfFretsToAdvance) >= nAbsSameNotesDifference)
                {
                    if (nNumberOfFretsToAdvance < 0)
                    {
                        String--;
                        nNumberOfFretsToAdvance += nAbsSameNotesDifference;
                        continue;
                        
                    }
                    else
                    {
                        if (String == EString.LastString)
                        {
                            return false;
                        }

                        String++;
                        nNumberOfFretsToAdvance -= nAbsSameNotesDifference;
                        continue;
                    }
                }

                if (nNumberOfFretsToAdvance < 0)
                {
                    // Check if the fret is the first one.
                    if (Fret == 0)
                    {
                        Fret += nSameNotesDifference;
                        String--;
                    }

                    Fret--;

                }
                else
                {

                    if (Fret == Globals.NUMBER_OF_FRETS)
                    {
                        Fret += nSameNotesDifference;
                        String++;
                    }

                    Fret++;
                }

                nNumberOfFretsToAdvance--;
            }

            return true;
        }

        public int SameNoteFretDifferenceOnString(EString eCurrent, EString eNext)
        {
            if (Math.Abs(eCurrent - eNext) != 1)
            {
                throw new Exception("Those strings are not close");
            }

            if ((eCurrent == EString.Second) && (eNext == EString.Third))
            {
                return 4;
            }

            if ((eCurrent == EString.Third) && (eNext == EString.Second))
            {
                return -4;
            }

            if (eCurrent < eNext)
            {
                return -5;
            }
            else
            {
                return 5;
            }
        }

        bool IsFirstPosition()
        {
            return this.Equals(Globals.s_posFirstGuitarPosition);
        }

        bool IsLastPosition()
        {
            return (Fret == Globals.NUMBER_OF_FRETS) &&
                   (String == EString.NumberOfStrings);
        }

        bool IsFirstPosition(CFingerPosition pos)
        {
            return pos.IsFirstPosition();
        }

        bool IsLastPosition(CFingerPosition pos)
        {
            return pos.IsLastPosition();
        }

        public CFingerPosition(EString eString, int nFret)
        {
            String = eString;
            Fret = nFret;
        }

        public bool Equals(CFingerPosition other)
        {
            return String == other.String &&
                    Fret == other.Fret;
        }
    };
}
